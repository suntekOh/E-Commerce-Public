using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using ECommerceAPI.Models;
using System.Globalization;
using System.Collections;
using System.Data.SqlClient;

namespace ECommerceAPI.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class ProductsController : ControllerBase
    {
        private readonly EcommerceContext _context;

        public ProductsController(EcommerceContext context)
        {
            _context = context;
        }


        // GET: api/products?keyword=keyword
        [HttpGet()]
        [ProducesResponseType(typeof(IEnumerable<ProductIncStock>), 200)]
        public async Task<IActionResult> GetProductsWhereKeyword([FromQuery] string keyword)
        {

            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }
            IEnumerable<Product> products;

            //If there is no keyword, show all products
            //Otherwise, show the products related to the keyword
            if (keyword == null || keyword.Trim().Length == 0)
            {
                products = await Task.FromResult<IEnumerable<Product>>(_context.Product.ToList());
            }
            else
            {
                products = await Task.FromResult<IEnumerable<Product>>(_context.Product.Where(a => a.Title.Contains(keyword)));
            }


            if (products == null)
            {
                return NotFound();
            }
            else
            {
                IList aList = new ArrayList();

                int inQty = 0;
                int outQty = 0;

                foreach (Product j in products)
                {
                    //Get Stock information of each product
                    inQty = _context.Inventory
                        .Where(a => a.ProductId.Equals(j.Id) && a.Type.Equals("I"))
                        .Select(a => a.Qty)
                        .DefaultIfEmpty(0)
                        .Sum();

                    outQty = _context.Inventory
                        .Where(a => a.ProductId.Equals(j.Id) && a.Type.Equals("O"))
                        .Select(a => a.Qty)
                        .DefaultIfEmpty(0)
                        .Sum();

                    aList.Add(new ProductIncStock
                    {
                        Id = j.Id,
                        Description = j.Description,
                        Price = j.Price,
                        Title = j.Title,
                        Pic = j.Pic,
                        CategoryId = j.CategoryId,
                        CreatedDttm = j.CreatedDttm,
                        Stock = inQty - outQty
                    });
                }


                return new ObjectResult(aList);
            }
        }

        [HttpGet("{id}")]
        [ProducesResponseType(typeof(IEnumerable<ProductIncStock>), 200)]
        public async Task<IActionResult> GetProductById([FromRoute] int id)
        {

            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }
            List<Product> products = new List<Product>();

            //Show product information related to the product ID.
            Product product = await _context.Product.FindAsync(id);
            products.Add(product);

            if (product == null)
            {
                return NotFound();
            }
            else
            {
                IList aList = new ArrayList();

                int inQty = 0;
                int outQty = 0;

                foreach (Product j in products)
                {
                    //Get Stock information of each product
                    inQty = _context.Inventory
                        .Where(a => a.ProductId.Equals(j.Id) && a.Type.Equals("I"))
                        .Select(a => a.Qty)
                        .DefaultIfEmpty(0)
                        .Sum();

                    outQty = _context.Inventory
                        .Where(a => a.ProductId.Equals(j.Id) && a.Type.Equals("O"))
                        .Select(a => a.Qty)
                        .DefaultIfEmpty(0)
                        .Sum();

                    aList.Add(new ProductIncStock
                    {
                        Id = j.Id,
                        Description = j.Description,
                        Price = j.Price,
                        Title = j.Title,
                        Pic = j.Pic,
                        CategoryId = j.CategoryId,
                        CreatedDttm = j.CreatedDttm,
                        Stock = inQty - outQty
                    });
                }


                return new ObjectResult(aList);
            }
        }


        // GET: api/products/recommend/5
        [HttpGet("recommend/{id}")]
        [ProducesResponseType(typeof(IEnumerable<ProductIncStock>), 200)]
        public async Task<IActionResult> GetMostSoldProductById([FromRoute] int id)
        {

            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }
            List<Product> products = new List<Product>();

            //Seek the most sold 3 products in the same category.
            var most_sold_products =
            _context.Inventory
                .Include(a => a.Product)
                .Where(a => a.Product.CategoryId
                            .Equals(_context.Product
                                            .Where(b => b.Id.Equals(id))
                                            .Select(b => b.CategoryId)
                                            .Max())
                             && a.Type.Equals("O")
                             && !a.ProductId.Equals(id)
                                            )
                .GroupBy(p => p.ProductId)
                .OrderByDescending(p => p.Sum(q => q.Qty))
                .Select(p => p.Max(q => q.ProductId))
                .Take(3)
                .ToList();

            foreach (int i in most_sold_products)
            {
                products.Add(await _context.Product.FindAsync(i));
            }

            IList aList = new ArrayList();

            int inQty = 0;
            int outQty = 0;

            foreach (Product j in products)
            {
                //Get Stock information of each product
                inQty = _context.Inventory
                    .Where(a => a.ProductId.Equals(j.Id) && a.Type.Equals("I"))
                    .Select(a => a.Qty)
                    .DefaultIfEmpty(0)
                    .Sum();

                outQty = _context.Inventory
                    .Where(a => a.ProductId.Equals(j.Id) && a.Type.Equals("O"))
                    .Select(a => a.Qty)
                    .DefaultIfEmpty(0)
                    .Sum();

                aList.Add(new ProductIncStock
                {
                    Id = j.Id,
                    Description = j.Description,
                    Price = j.Price,
                    Title = j.Title,
                    Pic = j.Pic,
                    CategoryId = j.CategoryId,
                    CreatedDttm = j.CreatedDttm,
                    Stock = inQty - outQty
                });
            }


            return new ObjectResult(aList);
        }


        //// POST: api/Products
        [HttpPost]
        [ProducesResponseType(typeof(Product), 201)]
        [ProducesResponseType(400)]
        public async Task<IActionResult> PostProductAsync([FromForm]ProductIncFile product)
        {

            if (product == null)
            {
                return BadRequest();
            }

            //If a user request have a file, upload this file to AWS S3.
            if (product.File != null)
            {
                product.Pic = UploadFile(product.File);
            }


            string strDttm = product.Pic.Substring(0, product.Pic.IndexOf('_'));
            DateTime createdDttm = DateTime.ParseExact(strDttm, "yyyyMMddHHmmss", CultureInfo.InvariantCulture);

            //Insert this new product information to AWS RDS product table.
            _context.Product.Add(new Product
            {
                Description = product.Description,
                Price = product.Price,
                Title = product.Title,
                Pic = product.Pic,
                CategoryId = product.CategoryId,
                CreatedDttm = createdDttm
            });
            await _context.SaveChangesAsync();

            //After getting the newly added data's Id,
            //Move to GetProductById operation having it as a parameter.
            var maxVal = _context.Product.Max(x => x.Id);
            return CreatedAtAction("GetProductById", new { id = maxVal });

        }

        // DELETE: api/Product/5
        [HttpDelete("{id}")]
        [ProducesResponseType(typeof(Product), 201)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status409Conflict)]
        public async Task<IActionResult> DeleteProduct([FromRoute] int id)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            var product = await _context.Product.FindAsync(id);


            //If there is no product related to the Id, Stop this operation.
            if (product == null)
            {
                return NotFound();
            }

            var pic = product.Pic;

            try
            {
                _context.Product.Remove(product);
                await _context.SaveChangesAsync();
            }
            catch (DbUpdateException ex)
            {

                return Conflict();
            }


            if (pic != null)
            {
                Upload2S3 aUpload2S3 = new Upload2S3();

                //Delete a picture file
                await aUpload2S3.DeleteProductAsync(pic);
            }

            return Ok(product);
        }

        private string UploadFile(IFormFile file)
        {

            //Add datetime value to file name to make sure there is no duplicated file name.
            //It is almost impossible for more than one users to upload the file having the same name at the same moment.
            var est = TimeZoneInfo.FindSystemTimeZoneById("Eastern Standard Time");
            var targetTime = TimeZoneInfo.ConvertTime(DateTime.Now, est);

            //IFormFile's FileName value depends on a browser
            //,so I add one more function to get a fileName.
            string filePath = file.FileName;
            string keyName = targetTime.ToString("yyyyMMddHHmmss_") + System.IO.Path.GetFileName(filePath);



            using (var fileStream = file.OpenReadStream())
            {
                if (fileStream == null)
                {
                    return null;
                }

                Upload2S3 aUpload2S3 = new Upload2S3();

                //Upload a file using stream value
                aUpload2S3.Upload(fileStream, keyName);

            }

            return keyName;
        }

        private bool ProductExists(int id)
        {
            return _context.Product.Any(e => e.Id == id);
        }
    }
}