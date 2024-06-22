terraform {
    required_providers {
    aws = {
        source  = "hashicorp/aws"
        version = "~> 5.3"
    }
    }
    required_version = "~> 1.7.0"
}

provider "aws" {
    region = "us-east-1"
}

# Creating a beanstalk application called TicTacToeApp
resource "aws_elastic_beanstalk_application" "tictactoe" {
        name        = "TicTacToeApp"
        description = "Dockerized tic tac toe app in Java Spring Boot + React"
}

# Crteating the beanstalk environment
resource "aws_elastic_beanstalk_environment" "my_env" {
        name                = "my-environment"
        application         = aws_elastic_beanstalk_application.tictactoe.name
        solution_stack_name = "64bit Amazon Linux 2 v3.8.0 running Docker"
        tier                = "WebServer"
        cname_prefix        = "olek-tictactoe"

    # Turning on the load balancer
    setting {
        namespace = "aws:elasticbeanstalk:environment"
        name      = "EnvironmentType"
        value     = "SingleInstance"
    }

    # Setting the launched EC2 instance type to t2.small
    setting {
        namespace = "aws:autoscaling:launchconfiguration"
        name      = "InstanceType"
        value     = "t2.small"
    }

    # Specifing key pair used for ssh authentication
    setting {
        namespace = "aws:autoscaling:launchconfiguration"
        name      = "EC2KeyName"
        value     = "ssh-key"
    }

    # Setting the IAM profile
    setting {
        namespace = "aws:autoscaling:launchconfiguration"
        name      = "IamInstanceProfile"
        value     = "LabInstanceProfile"
    }

    # Define VPC
    setting {
        namespace = "aws:ec2:vpc"
        name      = "VPCId"
        value     = aws_vpc.app_vpc.id
    }

    # Use public IP address
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

    # Define Security groups
    setting {
      namespace = "aws:autoscaling:launchconfiguration"
      name = "SecurityGroups"
      value = aws_security_group.app_sg.id
    }
}


# Create VPC for the environment
resource "aws_vpc" "app_vpc" {
  cidr_block = "10.0.0.0/16"
  enable_dns_support = true
  enable_dns_hostnames = true
}

# Create subnet for the environment
resource "aws_subnet" "app_subnet" {
  vpc_id     = aws_vpc.app_vpc.id
  cidr_block = "10.0.1.0/24"
  availability_zone = "us-east-1a"
}

# Create gateway for the environment
resource "aws_internet_gateway" "gateway" {
  vpc_id = aws_vpc.app_vpc.id
}

# Create route table for the environment
resource "aws_route_table" "route_table" {
  vpc_id = aws_vpc.app_vpc.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.gateway.id
  }
}

# Associate route table with subnet
resource "aws_route_table_association" "route_table_association" {
  subnet_id      = aws_subnet.app_subnet.id
  route_table_id = aws_route_table.route_table.id
}

resource "aws_security_group" "app_sg" {
  vpc_id = aws_vpc.app_vpc.id

  ingress {
    from_port   = 80
    to_port     = 81
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

  ingress {
    from_port   = 22
    to_port     = 22
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

