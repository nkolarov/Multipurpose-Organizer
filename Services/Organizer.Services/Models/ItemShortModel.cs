namespace Organizer.Services.Models
{
    using Organizer.Models;
    using System;
    using System.Linq.Expressions;
    using System.Runtime.Serialization;

    [DataContract]
    public class ItemShortModel
    {
        public static Expression<Func<Item, ItemShortModel>> FromItem
        {
            get
            {
                return item => new ItemShortModel
                {
                    Id = item.Id,
                    Title = item.Title,
                    ItemType = item.ItemType,
                    ParentId = item.ParentId,
                    ChildCount = item.Childrens.Count
                };
            }

        }
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