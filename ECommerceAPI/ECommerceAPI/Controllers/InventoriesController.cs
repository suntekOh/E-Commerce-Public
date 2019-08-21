using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using ECommerceAPI.Models;
using System.Transactions;

namespace ECommerceAPI.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class InventoriesController : ControllerBase
    {
        private readonly EcommerceContext _context;

        public InventoriesController(EcommerceContext context)
        {
            _context = context;
        }

        // GET: api/Inventories
        [HttpGet]
        public IEnumerable<Inventory> GetInventory()
        {
            return _context.Inventory;
        }

        // GET: api/Inventories/5
        [HttpGet("{id}")]
        public async Task<IActionResult> GetInventory([FromRoute] int id)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            var inventory = await _context.Inventory.FindAsync(id);

            if (inventory == null)
            {
                return NotFound();
            }

            return Ok(inventory);
        }

        // POST: api/Inventories
        [HttpPost]
        [ProducesResponseType(typeof(Inventory), 201)]
        public async Task<IActionResult> PostInventory([FromForm] Inventory inventory)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            var est = TimeZoneInfo.FindSystemTimeZoneById("Eastern Standard Time");
            DateTime createdDttm = TimeZoneInfo.ConvertTime(DateTime.Now, est);


            //Insert this new product information to AWS RDS product table.
            _context.Inventory.Add(new Inventory
            {
                Type = "I",
                CreatedDttm = createdDttm,
                Qty = inventory.Qty,
                CustomerId = inventory.CustomerId,
                ProductId = inventory.ProductId

            });
            await _context.SaveChangesAsync();

            return CreatedAtAction("GetInventory", new { id = inventory.Id }, inventory);
        }

        // Post: api/Inventories/order
        [HttpPost]
        [Route("order")]
        [ProducesResponseType(typeof(Inventory), 201)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status412PreconditionFailed)]
        public async Task<IActionResult> PostOrder([FromForm] Inventory inventory)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            //Check whether the ordered quantity is greater than the current stock,
            int inQty = _context.Inventory
                .Where(a => a.ProductId.Equals(inventory.ProductId) && a.Type.Equals("I"))
                .Select(a => a.Qty)
                .DefaultIfEmpty(0)
                .Sum();

            int outQty = _context.Inventory
                .Where(a => a.ProductId.Equals(inventory.ProductId) && a.Type.Equals("O"))
                .Select(a => a.Qty)
                .DefaultIfEmpty(0)
                .Sum();

            //If so, return Business exception
            if (inQty-outQty-inventory.Qty < 0)
            {
                return StatusCode(StatusCodes.Status412PreconditionFailed);

            }

            var est = TimeZoneInfo.FindSystemTimeZoneById("Eastern Standard Time");
            DateTime createdDttm = TimeZoneInfo.ConvertTime(DateTime.Now, est);

            //Get product price using ProductId
            decimal price4product = _context.Product.Where(a => a.Id.Equals(inventory.ProductId))
                .Select(a => a.Price).Max();

            try
            {
                using (var transaction = new TransactionScope(TransactionScopeAsyncFlowOption.Enabled))
                {

                    //Insert this new inventory information to AWS RDS inventory table.
                    _context.Inventory.Add(new Inventory
                    {
                        Type = "O",
                        CreatedDttm = createdDttm,
                        Qty = inventory.Qty,
                        CustomerId = inventory.CustomerId,
                        ProductId = inventory.ProductId

                    });
                    await _context.SaveChangesAsync();

                    //Insert this new inventory information to AWS RDS orderhistory table.
                    _context.OrderHistory.Add(new OrderHistory
                    {
                        Price = price4product,
                        Orderdate = createdDttm,
                        Qty = inventory.Qty,
                        CustomerId = inventory.CustomerId,
                        ProductId = inventory.ProductId

                    });
                    await _context.SaveChangesAsync();
                    transaction.Complete();
                }
                return CreatedAtAction("GetInventory", new { id = inventory.Id }, inventory);

            }
            catch (DbUpdateException ex)
            {
                ex.ToString();
                return Conflict();
            }

        }
        private bool InventoryExists(int id)
        {
            return _context.Inventory.Any(e => e.Id == id);
        }
    }
}