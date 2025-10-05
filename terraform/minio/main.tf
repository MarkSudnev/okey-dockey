terraform {
  required_providers {
    minio = {
      source = "aminueza/minio"
      version = "3.6.5"
    }
  }
}

provider "minio" {

}

resource "minio_s3_bucket" "dockey_bucket" {
    bucket = "dockey-bucket"
    acl    = "public"
}

resource "minio_s3_bucket_notification" "event_one" {
  depends_on = [ minio_s3_bucket.dockey_bucket ]
  bucket     = minio_s3_bucket.dockey_bucket.bucket
  queue {
    events    = ["s3:ObjectCreated:*"]
    queue_arn = "arn:minio:sqs::ONE:kafka"
  }
}
