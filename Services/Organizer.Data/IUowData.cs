namespace Organizer.Data
{
    using Organizer.Models;

    public interface IUowData
    {
        IRepository<Coordinates> Coordinates { get; }

        IRepository<Image> Images { get; }

        IRepository<Item> Items { get; }

        IRepository<Location> Locations { get; }

        IRepository<Note> Notes { get; }

        IRepository<User> Users { get; }

        int SaveChanges();
    }
}
