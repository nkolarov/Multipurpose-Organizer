namespace Organizer.Services.Models
{
    using System.Runtime.Serialization;

    [DataContract]
    public class LocationModel
    {
        [DataMember(Name = "id")]
        public decimal Id { get; set; }

        [DataMember(Name = "latitude")]
        public decimal Latitude { get; set; }

        [DataMember(Name = "longitude")]
        public decimal Longitude { get; set; }
    }
}