namespace Organizer.Services.Models
{
    using Organizer.Models;
    using System.Collections.Generic;
    using System.Runtime.Serialization;

    [DataContract]
    public class ItemDetailsModel
    {
        [DataMember(Name = "id")]
        public int Id { get; set; }

        [DataMember(Name = "title")]
        public string Title { get; set; }

        [DataMember(Name = "parented")]
        public int? ParentId { get; set; }

        [DataMember(Name = "itemtype")]
        public ItemType ItemType { get; set; }

        [DataMember(Name = "imagecount")]
        public int ImagesCount { get; set; }

        [DataMember(Name = "images")]
        public IEnumerable<ImageModel> Images { get; set; }

        [DataMember(Name = "notescount")]
        public int NotesCount { get; set; }

        [DataMember(Name = "notes")]
        public IEnumerable<NoteModel> Notes { get; set; }

        [DataMember(Name = "location")]
        public LocationModel Location { get; set; }
    }
}