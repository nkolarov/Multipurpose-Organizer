namespace Organizer.Services.Controllers
{
    using Organizer.Data;
    using Organizer.Models;
    using System;
    using System.Linq;
    using System.Net;
    using System.Net.Http;
    using System.Web.Http;

    public class BaseApiController : ApiController
    {
        protected IUowData Data;

        public BaseApiController(IUowData data)
        {
            this.Data = data;
        }

        public BaseApiController()
            : this(new UowData())
        {
        }

        protected T PerformOperationAndHandleExceptions<T>(Func<T> operation)
        {
            try
            {
                return operation();
            }
            catch (Exception ex)
            {
                var errResponse = this.Request.CreateErrorResponse(HttpStatusCode.BadRequest, ex.Message);
                throw new HttpResponseException(errResponse);
            }
        }

        protected User GetAndValidateUser(string sessionKey)
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