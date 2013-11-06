namespace Organizer.Services.Models
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Linq.Expressions;
    using System.Runtime.Serialization;
    using Organizer.Models;

    [DataContract]
    public class ItemModel
    {
        public static Expression<Func<Item, ItemModel>> FromItem
        {
            get
            {
                return item => new ItemModel
                {
                    Id = item.Id,
                    Title = item.Title,
                    ItemType = item.ItemType,
                    ParentId = item.ParentId,
                    ChildCount = item.Childrens.Count,
                    Childrens = 
                        from child in item.Childrens
                        select new ItemShortModel() 
                            {
                                Id = item.Id,
                                ItemType = item.ItemType,
                                ParentId = item.ParentId,
                                Title = item.Title,
                                ChildCount = item.Childrens.Count()
                            }
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

        [DataMember(Name = "childrens")]
        public IEnumerable<ItemShortModel> Childrens { get; set; }
    }
}