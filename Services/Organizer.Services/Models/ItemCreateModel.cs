using Organizer.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Web;

namespace Organizer.Services.Models
{
    public class ItemCreateModel
    {
        [DataMember(Name = "title")]
        public string Title { get; set; }

        [DataMember(Name = "itemtype")]
        public ItemType ItemType { get; set; }

        [DataMember(Name = "paerntid")]
        public int? ParentId { get; set; }
    }
}