namespace Organizer.Models
{
    using System.ComponentModel.DataAnnotations;

    public class User
    {
        [Key]
        public int Id { get; set; }

        [Required]
        [StringLength (30, MinimumLength=6)]
        public string Username { get; set; }

        [StringLength(30, MinimumLength = 6)]
        [Required]
        public string DisplayName { get; set; }

        [Required]
        public string AuthCode { get; set; }

        public string SessionKey { get; set; }
    }
}
