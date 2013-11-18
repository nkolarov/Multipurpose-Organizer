namespace Organizer.Services.Models
{
    using System.Runtime.Serialization;

    [DataContract]
    public class LocationModel
    {
        [DataMember(Name = "id")]
        public int Id { get; set; }

        [DataMember(Name = "latitude")]
        public double Latitude { get; set; }

        [DataMember(Name = "longitude")]
        public double Longitude { get; set; }
    }
}