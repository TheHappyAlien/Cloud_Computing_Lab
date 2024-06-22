terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 4.16"
    }
  }

  required_version = ">= 1.2.0"
}

# Specifies that the provider will be aws
provider "aws" {
  region  = "us-east-1"
}


resource "aws_s3_bucket" "my_s3_bucket" {
  bucket = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
}

resource "aws_s3_object" "docker_compose" {
  bucket = aws_s3_bucket.my_s3_bucket.bucket
  key    = "docker-compose.yml"
  source = "${path.module}/docker-compose.yml"
}

# Elastic Beanstalk application
resource "aws_elastic_beanstalk_application" "my_app" {
  name = "tictactoe-app"
}



# Elastic Beanstalk application version
resource "aws_elastic_beanstalk_application_version" "my_app_version" {
  name          = "v1"
  application   = aws_elastic_beanstalk_application.my_app.name
  description   = "Initial version"
  bucket        = aws_s3_bucket.my_s3_bucket.bucket
  key           = "docker-compose.yml"
  depends_on = [aws_elastic_beanstalk_application.my_app]
}

# Elastic Beanstalk environment
resource "aws_elastic_beanstalk_environment" "my_environment" {
  name                = "my-docker-env"
  application         = aws_elastic_beanstalk_application.my_app.id
  solution_stack_name = "64bit Amazon Linux 2023 v4.3.0 running Docker"

  setting {
    name      = "IamInstanceProfile"
    namespace = "aws:autoscaling:launchconfiguration"
    value     = "LabInstanceProfile"
  }

  depends_on = [
    aws_vpc.app_vpc,
    aws_subnet.app_subnet
  ]

  ## Service role
  setting {
    namespace = "aws:elasticbeanstalk:environment"
    name      = "ServiceRole"
    value     = "LabRole"
  }

  # Define instanceType
  setting {
    namespace = "aws:autoscaling:launchconfiguration"
    name      = "InstanceType"
    value     = "t2.small"
  }

  # Add listener port for backend 
  setting {
    namespace = "aws:elb:listener:81"
    name      = "ListenerProtocol"
    value     = "HTTP"
  }

  # Add listener port for backend 
  setting {
    namespace = "aws:elb:listener:8080"
    name      = "ListenerProtocol"
    value     = "HTTP"
  }

  setting {
    namespace = "aws:autoscaling:launchconfiguration"
    name      = "EC2KeyName"
    value     = "ssh-key"
  }

    # Define VPC
  setting {
    namespace = "aws:ec2:vpc"
    name      = "VPCId"
    value     = aws_vpc.app_vpc.id
  }

  setting {
    namespace = "aws:ec2:vpc"
    name      = "AssociatePublicIpAddress"
    value     = "true"
  }

  # Define Subnet
  setting {
    namespace = "aws:ec2:vpc"
    name      = "Subnets"
    value     = aws_subnet.app_subnet.id
  }
}




resource "aws_vpc" "app_vpc" {
  cidr_block           = "10.0.0.0/16"
  enable_dns_support   = true
  enable_dns_hostnames = true
  tags = {
    Name = "app_vpc"
  }
}

# Specifies the app uses the subnet 10.0.1.0/24
resource "aws_subnet" "app_subnet" {
  vpc_id            = aws_vpc.app_vpc.id
  cidr_block        = "10.0.1.0/24"
  availability_zone = "us-east-1a"
  tags = {
    Name = "app_subnet"
  }
}

resource "aws_internet_gateway" "app_gateway" {
  vpc_id = aws_vpc.app_vpc.id
}

resource "aws_route_table" "app_route_table" {
  vpc_id = aws_vpc.app_vpc.id
  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.app_gateway.id
  }
}

resource "aws_route_table_association" "my_route_table_association" {
  subnet_id      = aws_subnet.app_subnet.id
  route_table_id = aws_route_table.app_route_table.id
}

# Create a security group to control inbound and outbound traffic, and assign tags
resource "aws_security_group" "app_sg" {
  name        = "app_sg"
  description = "Allow ssh and web traffic"
  vpc_id      = aws_vpc.app_vpc.id

  # Allow connection to ports 22, 80, 81, and 8080 for all traffic, and allow all outward traffic
  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 81
    to_port     = 81
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}