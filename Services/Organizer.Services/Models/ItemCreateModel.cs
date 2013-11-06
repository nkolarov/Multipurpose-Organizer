namespace Organizer.Services.Models
{
    using System.Runtime.Serialization;
    using Organizer.Models;

    [DataContract]
    public class ItemCreateModel
    {
        [DataMember(Name = "title")]
        public string Title { get; set; }

        [DataMember(Name = "itemtype")]
        public ItemType ItemType { get; set; }

        [DataMember(Name = "parentid")]
        public int? ParentId { get; set; }
    }
}