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
        [HttpPost]
        [ActionName("add")]
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

                Coordinates coord = new Coordinates();
                coord.Latitude = locationModel.Latitude;
                coord.Longitude = locationModel.Longitude;

                Location location = new Location();
                location.Coordinates = coord;
                location.ItemId = itemModel.Id;
                this.Data.Locations.Add(location);

                return locationModel;
            });

            return responseMsg;
        }

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
                this.Data.Coordinates.Delete(locationModel.CoordinatesId);
                this.Data.SaveChanges();

                return Request.CreateResponse(HttpStatusCode.OK, locationModel);
            });

            return responseMsg;
        }

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

                if (locationEntry.Coordinates.Latitude != locationModel.Latitude)
                {
                    locationEntry.Coordinates.Latitude = locationModel.Latitude;
                }

                if (locationEntry.Coordinates.Longitude != locationModel.Longitude)
                {
                    locationEntry.Coordinates.Longitude = locationModel.Longitude;
                }

                this.Data.SaveChanges();

                return locationModel;
            });

            return responseMsg;
        }
    }
}