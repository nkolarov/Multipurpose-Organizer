using System;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using System.Web.Http.ValueProviders;
using Organizer.Services.Models;
using Organizer.Services.Attributes;
using Organizer.Models;

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
                var itemsEntities = this.Data.Items.All().Where(item => item.User == user).Select(ItemModel.FromItem);

                return itemsEntities.OrderByDescending(item => item.Id);
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
                var itemsEntities = this.GetAll(sessionKey).SingleOrDefault(item => item.ParentId == null)
                    .Childrens.AsQueryable().Where(item => item.ItemType == ItemType.Type);

                return itemsEntities.OrderByDescending(item => item.Id);
            });

            return responseMsg;
        }

        [HttpGet]
        [ActionName("typeLevel")]
        public IQueryable<ItemModel> GetTypesForParent(int parentID,
            [ValueProvider(typeof(HeaderValueProviderFactory<string>))] string sessionKey)
        {
            var responseMsg = this.PerformOperationAndHandleExceptions(() =>
            {

                var user = GetAndValidateUser(sessionKey);
                var itemsEntities = this.GetAll(sessionKey).SingleOrDefault(item => item.ParentId == parentID)
                    .Childrens.AsQueryable().Where(item => item.ItemType == ItemType.Type);

                return itemsEntities.OrderByDescending(item => item.Id);
            });

            return responseMsg;
        }

        [HttpGet]
        [ActionName("elementLevel")]
        public IQueryable<ItemModel> GetElementsForParent(int parentID,
            [ValueProvider(typeof(HeaderValueProviderFactory<string>))] string sessionKey)
        {
            var responseMsg = this.PerformOperationAndHandleExceptions(() =>
            {

                var user = GetAndValidateUser(sessionKey);
                var itemsEntities = this.GetAll(sessionKey).SingleOrDefault(item => item.ParentId == parentID)
                    .Childrens.AsQueryable().Where(item => item.ItemType == ItemType.Element);

                return itemsEntities.OrderByDescending(item => item.Id);
            });

            return responseMsg;
        }

        [HttpPost]
        [ActionName("add")]
        public ItemModel PostAddItem(int parentID, ItemCreateModel itemCreateModel,
            [ValueProvider(typeof(HeaderValueProviderFactory<string>))] string sessionKey)
        {
            var responseMsg = this.PerformOperationAndHandleExceptions(() =>
            {
                var user = GetAndValidateUser(sessionKey);

                Item item = new Item();
                item.Title = itemCreateModel.Title;
                item.ParentId = itemCreateModel.ParentId;
                item.ItemType = itemCreateModel.ItemType;
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

                var itemModel = this.Data.Items.All().Where(it => it.User == user).Select(ItemModel.FromItem).SingleOrDefault(it => it.Id == itemId);

                this.Data.Items.Delete(itemModel.Id);

                return Request.CreateResponse(HttpStatusCode.OK, itemModel);
            });

            return responseMsg;
        }

        [HttpPut]
        [ActionName("update")]
        public ItemModel PutUpdateItem(int itemId,
            [ValueProvider(typeof(HeaderValueProviderFactory<string>))] string sessionKey)
        {
            var responseMsg = this.PerformOperationAndHandleExceptions(() =>
            {
                var user = GetAndValidateUser(sessionKey);

                var itemModel = this.Data.Items.All().Where(it => it.User == user).Select(ItemModel.FromItem).SingleOrDefault(it => it.Id == itemId);

                var item = this.Data.Items.GetById(itemModel.Id);

                if (item.Title != itemModel.Title)
                {
                    item.Title = itemModel.Title;
                }

                if (item.ParentId != itemModel.ParentId)
                {
                    item.ParentId = itemModel.ParentId;
                }

                return itemModel;
            });

            return responseMsg;
        }

        private User GetAndValidateUser(string sessionKey)
        {
            var user = this.Data.Users.All().FirstOrDefault(usr => usr.SessionKey == sessionKey);
            if (user == null)
            {
                throw new InvalidOperationException("Invalid username or password!");
            }

            return user;
        }
    }
}