namespace Organizer.Data
{
    using System.Data.Entity;
    using Organizer.Models;

    public class OrganizerContext : DbContext
    {
        public OrganizerContext() : base("OrganizerDB") { }

        public DbSet<Coordinates> Coordinates { get; set; }

        public DbSet<Image> Images { get; set; }

        public DbSet<Item> Items { get; set; }

        public DbSet<ItemType> ItemTypes { get; set; }

        public DbSet<Location> Locations { get; set; }

        public DbSet<Note> Notes { get; set; }

        public DbSet<User> Users { get; set; }

        protected override void OnModelCreating(DbModelBuilder modelBuilder)
        {
            modelBuilder.Entity<Location>()
                .HasRequired(t => t.Item)
                .WithRequiredPrincipal(t => t.Location);

            modelBuilder.Entity<Coordinates>()
                .HasRequired(t => t.Location)
                .WithRequiredPrincipal(t => t.Coordinates);

            modelBuilder.Entity<User>()
                .Property(usr => usr.SessionKey)
                .IsFixedLength()
                .HasMaxLength(50);

            modelBuilder.Entity<User>()
                .Property(usr => usr.AuthCode)
                .IsFixedLength()
                .HasMaxLength(40);

            base.OnModelCreating(modelBuilder);
        }
    }
}
