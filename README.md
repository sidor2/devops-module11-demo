Basic Java application to build an image and deploy in a Kubernetes clusters. Used in devops-module11-eks repo.

- main: pushes to DockerHub, deploys to a local K8s cluster
- with-ecr: pushes to ECR, deploys to a local K8s cluster
- with-ecr-to-eks: pushes to ECR, deploys to EKS cluster. See https://github.com/sidor2/devops-module11-eks.git