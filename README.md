# ecommerce-projeto
## Controller

Aqui eu apresento o controlador para as requisições

```kotlin
@RestController
@RequestMapping("/ecommerce")
class Controller(
    @Autowired
    private val users : UserRepository,
    @Autowired
    private val products: ProductRepository
)
```

A anotação **`@RestController`** indica que essa classe é um controlador que irá lidar com requisições RESTful. A anotação **`@RequestMapping("/ecommerce")`** define que todas as rotas desse controlador terão o prefixo "/ecommerce".

A classe possui duas propriedades privadas **`users`** e **`products`**, que são instâncias das classes **`UserRepository`** e **`ProductRepository`**, respectivamente. Essas propriedades são anotadas com **`@Autowired`**, o que indica que elas serão injetadas pelo Spring Boot.

```kotlin
	@GetMapping("/number-of-products")
  fun getCountProducts(): Int = products.findAll().count()

  @GetMapping("/product/{id}")
  fun getProductById(@PathVariable("id") id: String): Product? = products.findByProductId(id)
```

Este código define dois endpoints HTTP usando a anotação **`@GetMapping`** em um controlador REST. O primeiro endpoint é **`/number-of-products`**, que retorna o número total de produtos armazenados no banco de dados. O método **`getCountProducts()`** usa o método **`findAll()`** do repositório de produtos para obter todos os produtos e retorna o resultado da função **`count()`**, que retorna o número de elementos na coleção.

O segundo endpoint é **`/number-of-products/{id}`**, que retorna um único produto do banco de dados com base em seu ID. O método **`getProductById()`** recebe o ID do produto como parâmetro de caminho e usa o método **`findByProductId()`** do repositório de produtos para procurar o produto com o ID correspondente. O método retorna o produto se ele for encontrado, ou **`null`** caso contrário.

Ambos os métodos usam injeção de dependência para obter instâncias dos repositórios de usuários e produtos, que são necessários para interagir com o banco de dados. A anotação **`@Autowired`** é usada para permitir que o Spring injete essas dependências automaticamente no controlador.

A mesma coisa acontece com as funções dos usuários para encontrar o número total de usuários e um usuário por Id:

```kotlin
	@GetMapping("/number-of-users")
  fun getCountUsers(): Int = users.findAll().count()

  @GetMapping("/user/{id}")
  fun getUserById(@PathVariable("id") id: String): User? = users.findByUserId(id)
```

Agora precisamos mudar o código para caso não seja encontrado um valor para Id em User ou Product

```kotlin
@GetMapping("/product/{id}")
    fun getProductById(@PathVariable("id") id: String): ResponseEntity<Product> =
        if (products.findByProductId(id) != null) ResponseEntity.ok(products.findByProductId(id))
        else ResponseEntity.notFound().build()
```

Neste trecho de código, temos um endpoint que recebe uma requisição GET na rota "/number-of-products/{id}", onde {id} é um parâmetro que será passado na URL. O objetivo desse endpoint é buscar um produto pelo seu id no banco de dados.

A anotação "@PathVariable" é usada para informar ao Spring que o parâmetro "id" deve ser extraído da URL da requisição. Em seguida, a função "getProductById" usa esse id para buscar um produto no banco de dados através do método "findByProductId" do repositório de produtos.

Se o produto é encontrado, a função retorna uma resposta HTTP com o status 200 OK e o objeto do produto no corpo da resposta. Caso contrário, retorna uma resposta com o status 404 Not Found.

A mesma coisa acontece com o código para User:

```kotlin
@GetMapping("/products/{id}")
fun getUserById(@PathVariable("id") id: String): ResponseEntity<User> =
	if (users.findByUserId(id) != null) ResponseEntity.ok(users.findByUserId(id))
  else ResponseEntity.notFound().build()
```

Agora vamos criar a função para adicionar um user e um product

```kotlin
@PostMapping("/add-user")
fun postUser(@RequestBody user: User) = ResponseEntity.ok(users.save(user))
```

Esse código está criando um novo usuário (User) quando um pedido (PostMapping) é feito para a rota "/add-user". O usuário é passado como um corpo de solicitação (RequestBody) no formato JSON. A função postUser está usando o método save do repositório (users) para salvar o novo usuário no banco de dados MongoDB. Quando o usuário é salvo com sucesso, o método ResponseEntity.ok retorna uma resposta HTTP 200 (sucesso) com o usuário salvo em formato JSON no corpo da resposta. Em resumo, esse código permite que os usuários sejam adicionados ao banco de dados do aplicativo através da rota "/add-user" usando JSON como formato de dados.

O código de product fica assim:

```kotlin
@PostMapping("/add-product")
fun postProduct(@RequestBody product: Product) = ResponseEntity.ok(products.save(product))
```

Em seguida precisamos de uma função que atualize o banco de dados:

```kotlin
@PatchMapping("update-product/{id}")
fun updateProduct(@PathVariable id: String, @RequestBody updates: Map<String, Any>): ResponseEntity<Product> {
	val product = products.findByProductId(id) ?: return ResponseEntity.notFound().build()

	val updatedProduct = updateProductFields(product, updates)
  products.save(updatedProduct)

  return ResponseEntity.ok(updatedProduct)
}

@PatchMapping("/update-user/{id}")
fun updateUser(@PathVariable id: String, @RequestBody updates: Map<String, Any>): ResponseEntity<User> {
	val user = users.findByUserId(id) ?: return ResponseEntity.notFound().build()

  val updatedUser = updateUserFields(user, updates)
  users.save(updatedUser)

  return ResponseEntity.ok(updatedUser)
}
```

Veja que aqui eu precisei recuperar o product ou user do banco de dados por meio do Id e com @ResquestBody eu utilizaria JSON para criar a atualização, porém vinha um problema, como atualizar apenas a propriedade desejada sem mudar nada anterior? Foi ai que criei a função seguinte, tanto para product como para user

```kotlin
// funções para atualização de user e product
private fun updateUserFields(user: User, updates: Map<String, Any>): User {
    var updatedUser = user.copy()

    for ((key, value) in updates) {
        when (key) {
            "userName" -> updatedUser = updatedUser.copy(userName = value as String)
            "password" -> updatedUser = updatedUser.copy(password = value as String)
            "email" -> updatedUser = updatedUser.copy(email = value as String)
            "address" -> {
                val addressMap = value as Map<*, *>
                val address = Address(
                    street = addressMap["street"] as String? ?: user.address?.street ?: "",
                    city = addressMap["city"] as String? ?: user.address?.city ?: "",
                    state = addressMap["state"] as String? ?: user.address?.state ?: "",
                    zipCode = addressMap["zipCode"] as String? ?: user.address?.zipCode ?: ""
                )
                updatedUser = updatedUser.copy(address = address)
            }
        }
    }

    return updatedUser
}

private fun updateProductFields(product: Product, updates: Map<String, Any>): Product {
    var updatedProduct = product.copy()

    for ((key, value) in updates) {
        when (key) {
            "productName" -> updatedProduct = updatedProduct.copy(productName = value as String)
            "productPreco" -> updatedProduct = updatedProduct.copy(productPreco = value as BigDecimal)
            "productDescription" -> updatedProduct = updatedProduct.copy(productDescription = value as String)
            "productType" -> updatedProduct = updatedProduct.copy(productType = value as String)
        }
    }

    return updatedProduct
}
```

E por último, uma função para deletar um user ou product do banco de dados passando Id para a url

```kotlin
@DeleteMapping("/delete-product/{id}")
fun deleteProduct(@PathVariable("id") id: String) {
	products.findByProductId(id)?.let {
		products.delete(it)
	}
}

@DeleteMapping("/delete-user/{id}")
fun deleteUser(@PathVariable("id") id: String) {
	users.findByUserId(id)?.let {
		users.delete(it)
	}
}
```
