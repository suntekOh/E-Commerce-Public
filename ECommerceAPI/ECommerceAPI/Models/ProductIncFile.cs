﻿using System;
using System.Collections.Generic;
using Microsoft.AspNetCore.Http;

namespace ECommerceAPI.Models
{
    public partial class ProductIncFile
    {

        public int Id { get; set; }
        public string Description { get; set; }
        public decimal Price { get; set; }
        public string Title { get; set; }
        public string Pic { get; set; }
        public DateTime CreatedDttm { get; set; }

        public int CategoryId { get; set; }
        public IFormFile File { get; set; }
    }
}
