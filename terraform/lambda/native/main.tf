provider "aws" {
  	region = var.aws_region
	profile = var.aws_profile
}

resource "aws_lambda_function" "marianoInsertToDynamoContact" {
	runtime		 					= "provided"
	description                     = "A basic lambda example to write into Contact's DynamoDB with Quarkus native (graalvm native-image)"
	source_code_hash 				= filebase64sha256("target/function.zip")
	filename	 					= "target/function.zip"
	function_name                  	= "arn:aws:lambda:us-east-1:161142984839:function:marianoInsertToDynamoContact"
	handler                        	= "io.quarkus.amazon.lambda.runtime.QuarkusStreamHandler::handleRequest"
	role                           	= "arn:aws:iam::161142984839:role/service-role/marianoInsertToDynamoContact-role-qduqbxrq"
	memory_size                    	= 512
	timeout                        	= 15

	environment {
		variables = {
			"DISABLE_SIGNAL_HANDLERS" 	= "true"
			"DYNAMO_TABLE_NAME" 		= "ContactMarianoLopez"
			"DYNAMO_REGION" 			= var.aws_region
		}
	}

    timeouts {}

    tracing_config {
        mode = "PassThrough"
    }
}
