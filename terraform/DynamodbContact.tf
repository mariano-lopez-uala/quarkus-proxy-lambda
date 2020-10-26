resource "aws_dynamodb_table" "ContactDynamo" {
  billing_mode     = "PROVISIONED"
  hash_key         = "id"
  name             = "ContactMarianoLopez"
  read_capacity    = 5
  stream_enabled   = true
  stream_view_type = "NEW_AND_OLD_IMAGES"
  tags             = {}
  write_capacity   = 5

  attribute {
    name = "id"
    type = "S"
  }

  point_in_time_recovery {
    enabled = false
  }

  timeouts {}

}