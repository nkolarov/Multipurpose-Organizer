namespace Organizer.Models
{
    using System;
    using System.ComponentModel.DataAnnotations;

    public class ItemType
    {
        [Key]
        public int Id { get; set; }

        [Required]
        public String Title { get; set; }
    }
}
