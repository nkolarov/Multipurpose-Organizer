namespace Organizer.Services.Models
{
    using System.Runtime.Serialization;

    [DataContract]
    public class ImageModel
    {
        [DataMember(Name = "id")]
        public int Id { get; set; }

        [DataMember(Name = "title")]
        public string Title { get; set; }

        [DataMember(Name = "imagedata")]
        public byte[] ImageData { get; set; }
    }
}