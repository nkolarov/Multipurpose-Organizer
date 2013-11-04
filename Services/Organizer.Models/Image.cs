namespace Organizer.Models
{
    using System.ComponentModel.DataAnnotations;

    public class Image
    {
        [Key]
        public int Id { get; set; }

        [Required]
        public string Title { get; set; }

        [Required]
        public byte[] ImageData { get; set; }

        [Required]
        public int ItemId { get; set; }

        public virtual Item Item { get; set; }

        public Image()
        {
            this.ImageData = new byte[] { };
        }
    }
}
