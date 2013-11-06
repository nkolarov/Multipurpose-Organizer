namespace Organizer.Services.Models
{
    using Organizer.Models;
    using System.Runtime.Serialization;

    [DataContract]
    public class ItemShortModel
    {
        [DataMember(Name = "id")]
        public int Id { get; set; }

        [DataMember(Name = "title")]
        public string Title { get; set; }

        [DataMember(Name = "itemtype")]
        public ItemType ItemType { get; set; }

        [DataMember(Name = "paerntid")]
        public int? ParentId { get; set; }

        [DataMember(Name = "childcount")]
        public int ChildCount { get; set; }
    }
}