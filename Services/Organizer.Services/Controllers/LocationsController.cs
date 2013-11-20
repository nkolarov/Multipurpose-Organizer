using Organizer.Models;
using Organizer.Services.Attributes;
using Organizer.Services.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using System.Web.Http.ValueProviders;

namespace Organizer.Services.Controllers
{
    public class LocationsController : BaseApiController
    {
        /// <summary>
        /// Sets the location of the item by given location model.
        /// </summary>
        /// <param name="itemId">The item id.</param>
        /// <param name="locationModel">A location model.</param>
        /// <param name="sessionKey">A session key.</param>
        /// <returns>The saved location.</returns>
        [HttpPost]
        [ActionName("set")]
        public LocationModel PostAddLocation(int itemId, LocationModel locationModel,
            [ValueProvider(typeof(HeaderValueProviderFactory<string>))] string sessionKey)
        {
            var responseMsg = this.PerformOperationAndHandleExceptions(() =>
            {
                var user = GetAndValidateUser(sessionKey);

                var itemModel = this.Data.Items.All().Where(item => item.UserId == user.Id).SingleOrDefault(it => it.Id == itemId);
                if (itemModel == null)
                {
                    throw new ArgumentException("Item Not Found!");
                }

                var locationEntry = this.Data.Locations.All().SingleOrDefault(n => n.ItemId == itemId);

                if (locationEntry == null)
                {
                    Location location = new Location();
                    location.ItemId = itemId;
                    location.Latitude = locationModel.Latitude;
                    location.Longitude = locationModel.Longitude;
                    itemModel.Location = location;
                    this.Data.Locations.Add(location);
                    this.Data.SaveChanges();
                }
                else
                {
                    if (locationEntry.Latitude != locationModel.Latitude)
                    {
                        locationEntry.Latitude = locationModel.Latitude;
                    }

                    if (locationEntry.Longitude != locationModel.Longitude)
                    {
                        locationEntry.Longitude = locationModel.Longitude;
                    }

                    this.Data.SaveChanges();
                }                

                return locationModel;
            });

            return responseMsg;
        }

        /// <summary>
        /// Deletes a location by given id.
        /// </summary>
        /// <param name="id">The location id.</param>
        /// <param name="sessionKey">A session key.</param>
        /// <returns>THe deleted location model.</returns>
        [HttpDelete]
        [ActionName("delete")]
        public HttpResponseMessage DeleteLocation(int id,
            [ValueProvider(typeof(HeaderValueProviderFactory<string>))] string sessionKey)
        {
            var responseMsg = this.PerformOperationAndHandleExceptions(() =>
            {
                var user = GetAndValidateUser(sessionKey);

                var locationModel = this.Data.Locations.All().Where(n => n.Item.User.Id == user.Id).SingleOrDefault(n => n.Id == id);
                if (locationModel == null)
                {
                    throw new ArgumentException("Location Not Found!");
                }

                this.Data.Locations.Delete(locationModel.Id);
                this.Data.SaveChanges();

                return Request.CreateResponse(HttpStatusCode.OK, locationModel);
            });

            return responseMsg;
        }

        /// <summary>
        /// Deletes a location by given location model.
        /// </summary>
        /// <param name="locationModel">The location model.</param>
        /// <param name="sessionKey">A session key.</param>
        /// <returns>THe updated location model.</returns>
        [HttpPut]
        [ActionName("update")]
        public LocationModel PutUpdateLocation(LocationModel locationModel,
            [ValueProvider(typeof(HeaderValueProviderFactory<string>))] string sessionKey)
        {
            var responseMsg = this.PerformOperationAndHandleExceptions(() =>
            {
                var user = GetAndValidateUser(sessionKey);

                var locationEntry = this.Data.Locations.All().Where(n => n.Item.User.Id == user.Id).SingleOrDefault(n => n.Id == locationModel.Id);
                if (locationModel == null)
                {
                    throw new ArgumentException("Note Not Found!");
                }

                if (locationEntry.Latitude != locationModel.Latitude)
                {
                    locationEntry.Latitude = locationModel.Latitude;
                }

                if (locationEntry.Longitude != locationModel.Longitude)
                {
                    locationEntry.Longitude = locationModel.Longitude;
                }

                this.Data.SaveChanges();

                return locationModel;
            });

            return responseMsg;
        }
    }
}