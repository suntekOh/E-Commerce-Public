using System;
using System.Collections.Generic;

namespace ECommerceAPI.Models
{
    public partial class Inventory
    {
        public int Id { get; set; }
        public string Type { get; set; }
        public DateTime CreatedDttm { get; set; }
        public int Qty { get; set; }
        public int CustomerId { get; set; }
        public int ProductId { get; set; }

        public Customer Customer { get; set; }
        public Product Product { get; set; }
    }
}
