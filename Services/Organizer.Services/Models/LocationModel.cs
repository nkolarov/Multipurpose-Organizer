namespace Organizer.Services.Models
{
    using System.Runtime.Serialization;

    [DataContract]
    public class LocationModel
    {
        [DataMember(Name = "latitude")]
        public decimal Latitude { get; set; }

        [DataMember(Name = "longitude")]
        public decimal Longitude { get; set; }
    }
}