using System;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using System.Web.Http.ValueProviders;
using Organizer.Services.Models;
using Organizer.Services.Attributes;
using Organizer.Models;
using System.Collections.Generic;

namespace Organizer.Services.Controllers
{
    public class ItemsController : BaseApiController
    {
        [HttpGet]
        [ActionName("all")]
        public IQueryable<ItemModel> GetAll(
            [ValueProvider(typeof(HeaderValueProviderFactory<string>))] string sessionKey)
        {
            var responseMsg = this.PerformOperationAndHandleExceptions(() =>
            {
                var user = GetAndValidateUser(sessionKey);
                var allItems = this.Data.Items.All();
                var allUserItems = allItems.Where(item => item.UserId == user.Id);
                var itemsEntities = allUserItems.Select(ItemModel.FromItem);

                return itemsEntities.OrderByDescending(item => item.Id);
            });

            return responseMsg;
        }

        [HttpGet]
        [ActionName("all-compact")]
        public IQueryable<ItemShortModel> GetAllShortInfo(
            [ValueProvider(typeof(HeaderValueProviderFactory<string>))] string sessionKey)
        {
            var responseMsg = this.PerformOperationAndHandleExceptions(() =>
            {
                var user = GetAndValidateUser(sessionKey);
                var allItems = this.Data.Items.All();
                var allUserItems = allItems.Where(item => item.UserId == user.Id);
                var itemsEntities = allUserItems.Select(ItemShortModel.FromItem);

                return itemsEntities.OrderByDescending(item => item.Id);
            });

            return responseMsg;
        }

        [HttpGet]
        [ActionName("forParent")]
        public IQueryable<ItemShortModel> GetShortItemsForParent(int? parentID,
            [ValueProvider(typeof(HeaderValueProviderFactory<string>))] string sessionKey)
        {
            var responseMsg = this.PerformOperationAndHandleExceptions(() =>
            {
                var user = GetAndValidateUser(sessionKey);
                var inParentId = parentID;
                if (parentID == null)
                {
                    // Get root elements
                    var rootElement = this.GetAll(sessionKey).SingleOrDefault(item => item.ParentId == null);
                    parentID = rootElement.Id;
                }

                var itemsEntities = this.GetAllShortInfo(sessionKey).Where(item => item.ParentId == parentID);

                if (inParentId != null)
                {
                    var rootElement = this.GetAllShortInfo(sessionKey).SingleOrDefault(item => item.Id == parentID);
                    var rootParentElement = this.GetAllShortInfo(sessionKey).SingleOrDefault(item => item.Id == rootElement.ParentId);
                    ItemShortModel LevelUp = new ItemShortModel();

                    LevelUp.Id = rootParentElement.Id;
                    LevelUp.Title = "... Level Up";
                    LevelUp.ParentId = rootParentElement.ParentId;
                    LevelUp.ChildCount = 0;
                    LevelUp.ItemType = ItemType.Type;

                    itemsEntities = itemsEntities.ToList().Concat(new List<ItemShortModel> { LevelUp }).AsQueryable();
                }

                return itemsEntities.OrderBy(item => item.ItemType).ThenBy(item => item.Title);
            });

            return responseMsg;
        }

        [HttpGet]
        [ActionName("rootLevel")]
        public IQueryable<ItemModel> GetTypesForRootLevel(
            [ValueProvider(typeof(HeaderValueProviderFactory<string>))] string sessionKey)
        {
            var responseMsg = this.PerformOperationAndHandleExceptions(() =>
            {
                var user = GetAndValidateUser(sessionKey);
                var rootElement = this.GetAll(sessionKey).SingleOrDefault(item => item.ParentId == null);
                var itemsEntities = this.GetAll(sessionKey).Where(item => item.ParentId == rootElement.Id).Where(item => item.ItemType == ItemType.Type);

                return itemsEntities.OrderByDescending(item => item.Id);
            });

            return responseMsg;
        }

        [HttpGet]
        [ActionName("typesAtLevel")]
        public IQueryable<ItemModel> GetTypesForParent(int parentID,
            [ValueProvider(typeof(HeaderValueProviderFactory<string>))] string sessionKey)
        {
            var responseMsg = this.PerformOperationAndHandleExceptions(() =>
            {

                var user = GetAndValidateUser(sessionKey);
                var itemsEntities = this.GetAll(sessionKey).Where(item => item.ParentId == parentID).Where(item => item.ItemType == ItemType.Type);

                return itemsEntities.OrderByDescending(item => item.Id);
            });

            return responseMsg;
        }

        [HttpGet]
        [ActionName("elementsAtLevel")]
        public IQueryable<ItemModel> GetElementsForParent(int parentID,
            [ValueProvider(typeof(HeaderValueProviderFactory<string>))] string sessionKey)
        {
            var responseMsg = this.PerformOperationAndHandleExceptions(() =>
            {

                var user = GetAndValidateUser(sessionKey);
                var itemsEntities = this.GetAll(sessionKey).Where(item => item.ParentId == parentID).Where(item => item.ItemType == ItemType.Element);

                return itemsEntities.OrderByDescending(item => item.Id);
            });

            return responseMsg;
        }

        [HttpGet]
        [ActionName("details")]
        public ItemDetailsModel GetElementDetails(int elementId,
            [ValueProvider(typeof(HeaderValueProviderFactory<string>))] string sessionKey)
        {
            var responseMsg = this.PerformOperationAndHandleExceptions(() =>
            {
                var user = GetAndValidateUser(sessionKey);

                var itemModelEntity = this.GetAll(sessionKey).Where(item => item.ItemType == ItemType.Element).SingleOrDefault(item => item.Id == elementId);

                if (itemModelEntity == null)
                {
                    throw new ArgumentException("Item Not Found!");
                }

                Item itemEntity = this.Data.Items.GetById(itemModelEntity.Id);
                ItemDetailsModel itemDetails = new ItemDetailsModel();

                itemDetails.Id = itemEntity.Id;
                itemDetails.ParentId = itemEntity.ParentId;
                itemDetails.Title = itemEntity.Title;
                itemDetails.ItemType = itemEntity.ItemType;
                itemDetails.ImagesCount = itemEntity.Images.Count();

                if (itemEntity.Images.Count() > 0)
                {
                    itemDetails.Images =
                        from image in itemEntity.Images
                        select new ImageModel()
                        {
                            Id = image.Id,
                            ImageData = image.ImageData,
                            Title = image.Title
                        };
                }

                itemDetails.NotesCount = itemEntity.Notes.Count();

                if (itemEntity.Notes.Count() > 0)
                {
                    itemDetails.Notes =
                        from note in itemEntity.Notes
                        select new NoteModel()
                        {
                            Id = note.Id,
                            Text = note.Text
                        };
                }

                if (itemEntity.Location != null)
                {
                    itemDetails.Location = new LocationModel()
                    {
                        Latitude = itemEntity.Location.Coordinates.Latitude,
                        Longitude = itemEntity.Location.Coordinates.Longitude
                    };
                }

                return itemDetails;
            });

            return responseMsg;
        }

        [HttpPost]
        [ActionName("add")]
        public ItemModel PostAddItem(ItemCreateModel itemCreateModel,
            [ValueProvider(typeof(HeaderValueProviderFactory<string>))] string sessionKey)
        {
            var responseMsg = this.PerformOperationAndHandleExceptions(() =>
            {
                var user = GetAndValidateUser(sessionKey);

                Item item = new Item();
                item.Title = itemCreateModel.Title;
                item.ParentId = itemCreateModel.ParentId;
                item.ItemType = itemCreateModel.ItemType;
                item.UserId = user.Id;
                this.Data.Items.Add(item);
                this.Data.SaveChanges();
                var itemModel = this.Data.Items.All().Select(ItemModel.FromItem).SingleOrDefault(it => it.Id ==item.Id);

                return itemModel;
            });

            return responseMsg;
        }

        [HttpDelete]
        [ActionName("delete")]
        public HttpResponseMessage DeleteItem(int itemId,
            [ValueProvider(typeof(HeaderValueProviderFactory<string>))] string sessionKey)
        {
            var responseMsg = this.PerformOperationAndHandleExceptions(() =>
            {
                var user = GetAndValidateUser(sessionKey);

                var itemModel = this.Data.Items.All().Where(it => it.UserId == user.Id).Select(ItemModel.FromItem).SingleOrDefault(it => it.Id == itemId);
                if (itemModel == null)
                {
                    throw new ArgumentException("Item Not Found!");
                }

                this.Data.Items.Delete(itemModel.Id);
                this.Data.SaveChanges();

                return Request.CreateResponse(HttpStatusCode.OK, itemModel);
            });

            return responseMsg;
        }

        [HttpPut]
        [ActionName("update")]
        public ItemUpdateModel PutUpdateItem(ItemUpdateModel itemUpdateModel,
            [ValueProvider(typeof(HeaderValueProviderFactory<string>))] string sessionKey)
        {
            var responseMsg = this.PerformOperationAndHandleExceptions(() =>
            {
                var user = GetAndValidateUser(sessionKey);

                var item = this.Data.Items.All().Where(it => it.UserId == user.Id).SingleOrDefault(it => it.Id == itemUpdateModel.Id);
                
                if (item.Title != itemUpdateModel.Title)
                {
                    item.Title = itemUpdateModel.Title;
                }

                if (item.ParentId != itemUpdateModel.ParentId)
                {
                    item.ParentId = itemUpdateModel.ParentId;
                }

                this.Data.SaveChanges();

                return itemUpdateModel;
            });

            return responseMsg;
        }        
    }
}