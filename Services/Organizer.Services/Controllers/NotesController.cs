namespace Organizer.Services.Controllers
{
    using System;
    using System.Linq;
    using System.Net;
    using System.Net.Http;
    using System.Web.Http;
    using System.Web.Http.ValueProviders;
    using Organizer.Models;
    using Organizer.Services.Attributes;
    using Organizer.Services.Models;

    public class NotesController : BaseApiController
    {
        /// <summary>
        /// Adds a note by given item id and note model.
        /// </summary>
        /// <param name="itemId">The item id.</param>
        /// <param name="noteModel">The note model.</param>
        /// <param name="sessionKey">A session key.</param>
        /// <returns>The created note model.</returns>
        [HttpPost]
        [ActionName("add")]
        public NoteModel PostAddNote(int itemId, NoteModel noteModel,
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

                Note note = new Note();
                note.Text = noteModel.Text;
                note.ItemId = itemModel.Id;
                this.Data.Notes.Add(note);
                this.Data.SaveChanges();
                noteModel.Id = note.Id;

                return noteModel;
            });

            return responseMsg;
        }

        /// <summary>
        /// Deletes a note by given id.
        /// </summary>
        /// <param name="id">The id.</param>
        /// <param name="sessionKey">A session key.</param>
        /// <returns>The deleted note model.</returns>
        [HttpDelete]
        [ActionName("delete")]
        public HttpResponseMessage DeleteNote(int id,
            [ValueProvider(typeof(HeaderValueProviderFactory<string>))] string sessionKey)
        {
            var responseMsg = this.PerformOperationAndHandleExceptions(() =>
            {
                var user = GetAndValidateUser(sessionKey);

                var noteModel = this.Data.Notes.All().Where(n => n.Item.User.Id == user.Id).SingleOrDefault(n => n.Id == id);
                if (noteModel == null)
                {
                    throw new ArgumentException("Note Not Found!");
                }

                this.Data.Notes.Delete(noteModel.Id);
                this.Data.SaveChanges();

                return Request.CreateResponse(HttpStatusCode.OK, noteModel);
            });

            return responseMsg;
        }

        /// <summary>
        /// Deletes a note by given id and note model.
        /// </summary>
        /// <param name="id">The id.</param>
        /// <param name="sessionKey">A session key.</param>
        /// <returns>The updated note model.</returns>
        [HttpPut]
        [ActionName("update")]
        public NoteModel PutUpdateNote(NoteModel noteModel,
            [ValueProvider(typeof(HeaderValueProviderFactory<string>))] string sessionKey)
        {
            var responseMsg = this.PerformOperationAndHandleExceptions(() =>
            {
                var user = GetAndValidateUser(sessionKey);

                var noteEntity = this.Data.Notes.All().Where(n => n.Item.User.Id == user.Id).SingleOrDefault(n => n.Id == noteModel.Id);
                if (noteModel == null)
                {
                    throw new ArgumentException("Note Not Found!");
                }

                if (noteEntity.Text != noteModel.Text)
                {
                    noteEntity.Text = noteModel.Text;
                }

                this.Data.SaveChanges();

                return noteModel;
            });

            return responseMsg;
        }
    }
}