using System;
using System.Collections.Generic;

namespace ECommerceAPI.Models
{
    public partial class Customer
    {
        public Customer()
        {
            Inventory = new HashSet<Inventory>();
            OrderHistory = new HashSet<OrderHistory>();
        }

        public int Id { get; set; }
        public string Email { get; set; }
        public string Password { get; set; }
        public string Type { get; set; }
        public DateTime CreatedDttm { get; set; }

        public ICollection<Inventory> Inventory { get; set; }
        public ICollection<OrderHistory> OrderHistory { get; set; }
    }
}
