using Organizer.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Linq.Expressions;
using System.Runtime.Serialization;
using System.Web;

namespace Organizer.Services.Models
{
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
                    Childrens = item.Childrens.AsQueryable().Select(ItemModel.FromItem)
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
        public IEnumerable<ItemModel> Childrens { get; set; }
    }
}