namespace Organizer.Services.Models
{
    using Organizer.Models;
    using System.Collections.Generic;
    using System.Runtime.Serialization;

    [DataContract]
    public class ItemDetailsModel
    {
        [DataMember(Name = "imagedata")]
        public int Id { get; set; }

        [DataMember(Name = "imagedata")]
        public string Title { get; set; }

        [DataMember(Name = "imagedata")]
        public int? ParentId { get; set; }

        [DataMember(Name = "imagedata")]
        public ItemType ItemType { get; set; }

        [DataMember(Name = "imagedata")]
        public int ImagesCount { get; set; }

        [DataMember(Name = "imagedata")]
        public IEnumerable<ImageModel> Images { get; set; }

        [DataMember(Name = "imagedata")]
        public int NotesCount { get; set; }

        [DataMember(Name = "imagedata")]
        public IEnumerable<NoteModel> Notes { get; set; }

        [DataMember(Name = "imagedata")]
        public LocationModel Location { get; set; }
    }
}