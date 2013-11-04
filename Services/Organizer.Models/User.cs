namespace Organizer.Models
{
    using System.ComponentModel.DataAnnotations;

    public class User
    {
        [Key]
        public int Id { get; set; }

        [Required]
        public string Username { get; set; }

        [Required]
        public string NickName { get; set; }

        [Required]
        public string AuthCode { get; set; }

        public string SessionKey { get; set; }

    }
}
