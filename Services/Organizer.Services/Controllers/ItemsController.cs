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
        /// <summary>
        /// Get all items of the current user. User is determinited by the session key.
        /// </summary>
        /// <param name="sessionKey">A session key.</param>
        /// <returns>All items.</returns>
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

        /// <summary>
        /// Get compact info for all items of the current user. User is determinited by the session key.
        /// </summary>
        /// <param name="sessionKey">A session key.</param>
        /// <returns>All items.</returns>
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

        /// <summary>
        /// Get all items of the current user for given parentId. User is determinited by the session key.
        /// </summary>
        /// <param name="sessionKey">A session key.</param>
        /// <returns>All items that have the given parentId.</returns>
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

        /// <summary>
        /// Get the details for given item. 
        /// </summary>
        /// <param name="sessionKey">A session key.</param>
        /// <returns>The details for the item..</returns>
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
                itemDetails.NotesCount = itemEntity.Notes.Count();

                if (itemEntity.Notes.Count() > 0)
                {
                    itemDetails.Notes = string.Join("\n", 
                        from note in itemEntity.Notes
                        select note.Text);
                }

                if (itemEntity.Location != null)
                {
                    itemDetails.Location = new LocationModel()
                    {
                        Latitude = itemEntity.Location.Latitude,
                        Longitude = itemEntity.Location.Longitude
                    };
                }

                return itemDetails;
            });

            return responseMsg;
        }

        /// <summary>
        /// Adds an item by given model.
        /// </summary>
        /// <param name="itemCreateModel">The model.</param>
        /// <param name="sessionKey">A session key.</param>
        /// <returns>The created item.</returns>
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
                if (itemCreateModel.ParentId == 0 || itemCreateModel.ParentId == null)
                {
                    var rootUserElementId = this.GetAll(sessionKey).SingleOrDefault(it => it.ParentId == null).Id;
                    item.ParentId = rootUserElementId;
                }
                else
                {
                    item.ParentId = itemCreateModel.ParentId;
                }
                
                item.ItemType = itemCreateModel.ItemType;
                item.UserId = user.Id;
                this.Data.Items.Add(item);
                this.Data.SaveChanges();
                var itemModel = this.Data.Items.All().Select(ItemModel.FromItem).SingleOrDefault(it => it.Id ==item.Id);

                return itemModel;
            });

            return responseMsg;
        }

        /// <summary>
        /// Deletes an item recursively by given itemId.
        /// </summary>
        /// <param name="itemId">The itemId.</param>
        /// <param name="sessionKey">A session key.</param>
        /// <returns>The deleted item model.</returns>
        [HttpDelete]
        [ActionName("delete")]
        public HttpResponseMessage DeleteItem(int itemId,
            [ValueProvider(typeof(HeaderValueProviderFactory<string>))] string sessionKey)
        {
            var responseMsg = this.PerformOperationAndHandleExceptions(() =>
            {
                var user = GetAndValidateUser(sessionKey);

                var itemModel = this.Data.Items.All().Where(it => it.UserId == user.Id).Select(ItemShortModel.FromItem).SingleOrDefault(it => it.Id == itemId);
                if (itemModel == null)
                {
                    throw new ArgumentException("Item Not Found!");
                }

                DeleteItem(itemId);
                this.Data.SaveChanges();

                return Request.CreateResponse(HttpStatusCode.OK, itemModel);
            });

            return responseMsg;
        }

        /// <summary>
        /// Deletes an item recursively by given model.
        /// </summary>
        /// <param name="itemUpdateModel">The model.</param>
        /// <param name="sessionKey">A session key.</param>
        /// <returns>The updated item model.</returns>
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

        private void DeleteItem(int itemId)
        {
            var item = this.Data.Items.All().SingleOrDefault(it => it.Id == itemId);
            if (item.Location != null)
            {
                this.Data.Locations.Delete(item.Location);
                item.Location = null;
                item.LocationId = null;
            }

            while (item.Images.Count > 0)
            {
                this.Data.Images.Delete(item.Images.First());
            }

            while (item.Notes.Count > 0)
            {
                this.Data.Notes.Delete(item.Notes.First());
            }

            while (item.Childrens.Count > 0)
            {
                DeleteItem(item.Childrens.First().Id);
            }

            this.Data.Items.Delete(item);
        }
    }
}