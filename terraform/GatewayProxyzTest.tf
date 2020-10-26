resource "aws_api_gateway_rest_api" "ProxyzTest" {
  api_key_source           = "HEADER"
  binary_media_types       = []
  description              = "Proxy Gateway"
  minimum_compression_size = -1
  name                     = "proxyzTest-API"
  tags                     = {}

  endpoint_configuration {
    types            = [
      "REGIONAL",
    ]
  }

}