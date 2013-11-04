﻿namespace Organizer.Models
{
    using System.ComponentModel.DataAnnotations;

    public class Coordinates
    {
        [Key]
        public int Id { get; set; }

        [Required]
        public decimal Latitude { get; set; }

        [Required]
        public decimal Longitude { get; set; }
    }
}