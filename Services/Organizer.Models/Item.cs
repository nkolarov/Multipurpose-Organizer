namespace Organizer.Models
{
    using System.Collections.Generic;
    using System.ComponentModel.DataAnnotations;

    public class Item
    {
        [Key]
        public int Id { get; set; }

        [Required]
        public string Title { get; set; }

        [Required]
        public int ItemTypeId { get; set; }

        public virtual ItemType ItemType { get; set; }

        public int ParentId { get; set; }

        public virtual Item Parent { get; set; }

        [Required]
        public int UserId { get; set; }

        public virtual User User { get; set; }

        public int LocationId { get; set; }

        public virtual Location Location { get; set; }

        public ICollection<Item> Childrens { get; set; }

        public ICollection<Note> Notes { get; set; }

        public ICollection<Image> Images { get; set; }

        public Item()
        {
            this.Childrens = new HashSet<Item>();
            this.Notes = new HashSet<Note>();
            this.Images = new HashSet<Image>();
        }
    }
}
