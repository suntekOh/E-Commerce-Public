using System;
using System.Collections.Generic;

namespace ECommerceAPI.Models
{
    public partial class Product
    {
        public Product()
        {
            Inventory = new HashSet<Inventory>();
            OrderHistory = new HashSet<OrderHistory>();
        }

        public int Id { get; set; }
        public string Description { get; set; }
        public decimal Price { get; set; }
        public string Title { get; set; }
        public string Pic { get; set; }
        public DateTime CreatedDttm { get; set; }
        public int CategoryId { get; set; }

        public Category Category { get; set; }
        public ICollection<Inventory> Inventory { get; set; }
        public ICollection<OrderHistory> OrderHistory { get; set; }
    }
}
