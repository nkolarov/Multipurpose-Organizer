namespace Organizer.Models
{
    using System.ComponentModel.DataAnnotations;

    public class Location
    {
        [Key]
        public int Id { get; set; }

        [Required]
        public int CoordinatesId { get; set; }

        public virtual Coordinates Coordinates { get; set; }

        [Required]
        public int ItemId { get; set; }

        public virtual Item Item { get; set; }
    }
}
