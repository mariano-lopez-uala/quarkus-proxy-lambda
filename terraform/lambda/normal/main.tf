provider "aws" {
  	region = var.aws_region
	profile = var.aws_profile
}

resource "aws_lambda_function" "proxyzTest" {
	description                    = "Proxy-lambda"
	function_name                  = "arn:aws:lambda:us-east-1:161142984839:function:proxyzTest"
	handler                        = "io.quarkus.amazon.lambda.runtime.QuarkusStreamHandler::handleRequest"
	memory_size                    = 512
	reserved_concurrent_executions = -1
	role                           = "arn:aws:iam::161142984839:role/service-role/proxyzTest-role-ykunx2j2"
	runtime                        = "java11"
	source_code_hash               = filebase64sha256("../../../target/lambda-proxy-1.0.0-SNAPSHOT-runner.jar")
	filename	 				   = "../../../target/lambda-proxy-1.0.0-SNAPSHOT-runner.jar"
	tags                           = {}
	timeout                        = 10

	environment {
		variables = {
			"DISABLE_SIGNAL_HANDLERS" = "true"
			"DYNAMO_TABLE_NAME"       = "ContactMarianoLopez"
		}
	}

	timeouts {}

	tracing_config {
		mode = "PassThrough"
	}

}