namespace Organizer.Models
{
    using System.ComponentModel.DataAnnotations;
    using System.ComponentModel.DataAnnotations.Schema;

    public class Note
    {
        [Key]
        public int Id { get; set; }

        [Required]
        [Column(TypeName = "ntext")]
        public string Text { get; set; }

        [Required]
        public int ItemId { get; set; }

        public virtual Item Item { get; set; }
    }
}
