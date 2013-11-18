namespace Organizer.Models
{
    using System.ComponentModel.DataAnnotations;

    public class Location
    {
        [Key]
        public int Id { get; set; }

        [Required]
        public double Latitude { get; set; }

        [Required]
        public double Longitude { get; set; }

        [Required]
        public int ItemId { get; set; }

        public virtual Item Item { get; set; }
    }
}
