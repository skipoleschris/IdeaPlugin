package uk.co.skipoles.eventmodeller.util

fun makeShortName(name: String): String {
  if (!name.contains('.')) return name

  val className = name.split('.').last()
  val messageName =
      if (className.endsWith("Command")) className.dropLast(7)
      else if (className.endsWith("Event")) className.dropLast(5)
      else if (className.endsWith("Query")) className.dropLast(5)
      else if (className.endsWith("Saga")) className.dropLast(4) else className
  return messageName
      .split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])".toRegex())
      .joinToString(" ")
}
