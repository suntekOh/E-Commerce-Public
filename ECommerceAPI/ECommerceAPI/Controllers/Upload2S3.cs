using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using Amazon;
using Amazon.S3;
using Amazon.S3.Model;
using Amazon.S3.Transfer;

namespace ECommerceAPI.Controllers
{
    public class Upload2S3
    {
        private string bucketName = "image4ecommerce";
        private string DirectoryTosave = Environment.GetFolderPath(Environment.SpecialFolder.UserProfile) + "\\Downloads\\";
        private readonly RegionEndpoint bucketRegion = RegionEndpoint.USEast2;
        private IAmazonS3 s3Client;


        public Upload2S3()
        {
            s3Client = new AmazonS3Client("AKIARVSCLMYQHNI2RVUB", "UIHNdqKAJgcYgl+k1DdlBtZ0pyUbQVcbTMM+4Clg", bucketRegion);
        }

        //Upload a file using stream value.
        public void Upload(Stream aStream, string objectKey)
        {
            try
            {
                var util = new TransferUtility(s3Client);
                util.Upload(aStream, bucketName, objectKey);

            }
            catch (AmazonS3Exception e)
            {
                Console.WriteLine("Error encounterd on server. Message:'{0}' when uploading an object", e.Message);
            }
            catch (Exception e)
            {
                Console.WriteLine("Unknown encounterd on server. Message:'{0}' when uploading an object", e.Message);
            }

        }

        public async Task DeleteProductAsync(string keyName)
        {
            DeleteObjectResponse x = await s3Client.DeleteObjectAsync(bucketName, keyName);
        }

        //Bring stream value from Amazon S3.
        public Stream ReadStream(string objectKey)
        {
            try
            {
                var util = new TransferUtility(s3Client);
                return util.OpenStream(bucketName, objectKey);

            }
            catch (AmazonS3Exception e)
            {
                Console.WriteLine("Error encounterd on server. Message:'{0}' when streaming an object", e.Message);
                return null;
            }
            catch (Exception e)
            {
                Console.WriteLine("Unknown encounterd on server. Message:'{0}' when streaming an object", e.Message);
                return null;
            }

        }



    }
}
