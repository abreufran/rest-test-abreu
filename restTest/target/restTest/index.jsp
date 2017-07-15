<html>
<body>
<h2>Rest Test!</h2>
<form id="formEjercicio1" method="POST" action="rest/RestService/wordHtml">
  <h2>Ejercicio 1: Esta seccion es un cliente Json que llama al Servicio del ejercicio 1</h2>
  <input type="text" required name="data" ></input>
  <button type="submit">Probar Ejercicio 1</button>  
</form>

  <h2>Ejercicio 2: Se llama al servicio del ejercicio 2 con hora igual a 15</h2>
  <a href="rest/RestService/time?value=15"> Probar Ejercicio 2</a> 

</body>
</html>
