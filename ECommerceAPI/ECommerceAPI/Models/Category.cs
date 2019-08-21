using System;
using System.Collections.Generic;

namespace ECommerceAPI.Models
{
    public partial class Category
    {
        public Category()
        {
            Product = new HashSet<Product>();
        }

        public int Id { get; set; }
        public string Descriptions { get; set; }

        public ICollection<Product> Product { get; set; }
    }
}
