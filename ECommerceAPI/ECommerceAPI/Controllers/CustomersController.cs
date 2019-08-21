using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using ECommerceAPI.Models;

namespace ECommerceAPI.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class CustomersController : ControllerBase
    {
        private readonly EcommerceContext _context;

        public CustomersController(EcommerceContext context)
        {
            _context = context;
        }

        //// GET: api/Customers
        //[HttpGet]
        //public IEnumerable<Customer> GetCustomer()
        //{
        //    return _context.Customer;
        //}

        // GET: api/Customers/5
        [HttpGet("{id}")]
        public async Task<IActionResult> GetCustomer([FromRoute] int id)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            var customer = await _context.Customer.FindAsync(id);


            if (customer == null)
            {
                return NotFound();
            }

            return Ok(customer);
        }

        // GET: api/customers?email=email
        [HttpGet()]
        [ProducesResponseType(typeof(IEnumerable<Customer>), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status409Conflict)]
        public async Task<IActionResult> GetCustomerByEmail([FromQuery] string email, string password)
        {
            

            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }


            IEnumerable <Customer> rtrnCustomers;

            if (email == null || email.Trim().Length == 0)
            {
                return NotFound();

            }
            else
            {
                rtrnCustomers = await Task.FromResult<IEnumerable<Customer>>(_context.Customer.Where(i => i.Email.Equals(email) && i.Password.Equals(password)).ToList());
            }

            if (rtrnCustomers.Count() <= 0)
            {
                var isEmailStored = await Task.FromResult<IEnumerable<Customer>>(_context.Customer.Where(i => i.Email.Equals(email)).ToList());
                //If the entered user email exist and the entered password is wrong, send HTTP409 message.
                if (isEmailStored.Count() > 0)
                {

                    return Conflict();
                }
                //IF the entered user email doesn't exist, send HTTP404 message.
                else
                {
                    return NotFound();

                }

            }
            else
            {
                return new ObjectResult(rtrnCustomers);
            }
        }

        // GET: api/customers/check4duplication?email=email
        [HttpGet()]
        [Route("check4duplicate")]
        [ProducesResponseType(typeof(IEnumerable<Customer>), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public async Task<IActionResult> Check4duplication([FromQuery] string email)
        {


            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }


            IEnumerable<Customer> isEmailRegistered;

            if (email == null || email.Trim().Length == 0)
            {
                return NotFound();

            }
            else
            {
                isEmailRegistered = await Task.FromResult<IEnumerable<Customer>>(_context.Customer.Where(i => i.Email.Equals(email)).ToList());
            }

            //If email doesn't exist, send HTTP404 message
            if (isEmailRegistered.Count() <= 0)
            {
                 return NotFound();

            }
            //Otherwise, send HTTP200 message.
            else
            {
                return new ObjectResult(isEmailRegistered);
            }
        }

        // POST: api/Customers
        [HttpPost]
        [ProducesResponseType(typeof(Customer), 201)]
        public async Task<IActionResult> PostCustomer([FromForm] Customer customer)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            var est = TimeZoneInfo.FindSystemTimeZoneById("Eastern Standard Time");
            DateTime createdDttm = TimeZoneInfo.ConvertTime(DateTime.Now, est);


            //Insert this new product information to AWS RDS product table.
            _context.Customer.Add(new Customer
            {
                Email = customer.Email,
                Password= customer.Password,
                Type = customer.Type,
                CreatedDttm = createdDttm
            });
            await _context.SaveChangesAsync();

            return CreatedAtAction("GetCustomer", new { id = customer.Id }, customer);
        }
               
        private bool CustomerExists(int id)
        {
            return _context.Customer.Any(e => e.Id == id);
        }
    }
}