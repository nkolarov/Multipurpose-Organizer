using Organizer.Data;
using Organizer.Data.Migrations;
using Organizer.Models;
using System;
using System.Collections.Generic;
using System.Data.Entity;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Organizer.DBInitializer
{
    public class DBInitializer
    {
        static void Main(string[] args)
        {
            Database.SetInitializer(new MigrateDatabaseToLatestVersion<OrganizerContext, Configuration>());

            using (var context = new OrganizerContext())
            {

                var user = context.Users.FirstOrDefault(u => u.Username == "bayIvan4");

                if (user == null)
                {
                    user = new User();
                    user.AuthCode = "0123456789012345678901234567890123456789";
                    user.DisplayName = "Ivan Ivanov2";
                    user.Username = "bayIvan4";

                    context.Users.Add(user);
                    context.SaveChanges();
                }
                    Item rootUserItem = new Item();
                    rootUserItem.ItemType = ItemType.Type;
                    rootUserItem.Title = "Root";
                    rootUserItem.User = user;

                    context.Items.Add(rootUserItem);

                    context.SaveChanges();
                
            }
        }
    }
}
