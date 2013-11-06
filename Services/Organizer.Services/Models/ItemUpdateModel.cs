using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Web;

namespace Organizer.Services.Models
{
    [DataContract]
    public class ItemUpdateModel
    {
        [DataMember(Name = "id")]
        public int Id { get; set; }

        [DataMember(Name = "title")]
        public string Title { get; set; }

        [DataMember(Name = "parentid")]
        public int? ParentId { get; set; }
    }
}