namespace Organizer.Services.Models
{
    using System.Runtime.Serialization;

    [DataContract]
    public class NoteModel
    {
        [DataMember(Name = "id")]
        public int Id { get; set; }

        [DataMember(Name = "text")]
        public string Text { get; set; }
    }
}