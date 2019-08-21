using System;
using System.Collections.Generic;

namespace ECommerceAPI.Models
{
    public partial class OrderHistory
    {
        public int Id { get; set; }
        public DateTime Orderdate { get; set; }
        public int Qty { get; set; }
        public decimal Price { get; set; }
        public int CustomerId { get; set; }
        public int ProductId { get; set; }

        public Customer Customer { get; set; }
        public Product Product { get; set; }
    }
}
