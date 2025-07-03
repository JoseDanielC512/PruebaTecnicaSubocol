from abc import ABC, abstractmethod

class ComparisonStrategy(ABC):
    """
    Interfaz para definir una estrategia de comparación de Pokémon.
    """
    @abstractmethod
    def compare(self, pokemon1_stats, pokemon2_stats):
        """
        Compara dos Pokémon según una estrategia específica.

        :param pokemon1_stats: Diccionario con las estadísticas del primer Pokémon.
        :param pokemon2_stats: Diccionario con las estadísticas del segundo Pokémon.
        :return: Una cadena de texto con el resultado de la comparación.
        """
        pass

class SumOfBaseStatsStrategy(ComparisonStrategy):
    """
    Estrategia que compara Pokémon basándose en la suma de sus estadísticas base
    (HP, Ataque, Defensa y Velocidad).
    """
    def compare(self, pokemon1_stats, pokemon2_stats):
        """
        Calcula la suma de las estadísticas base y determina el Pokémon más fuerte.
        """
        stats1_sum = pokemon1_stats['hp'] + pokemon1_stats['ataque'] + pokemon1_stats['defensa'] + pokemon1_stats['velocidad']
        stats2_sum = pokemon2_stats['hp'] + pokemon2_stats['ataque'] + pokemon2_stats['defensa'] + pokemon2_stats['velocidad']

        pokemon1_name = pokemon1_stats['nombre'].capitalize()
        pokemon2_name = pokemon2_stats['nombre'].capitalize()

        if stats1_sum > stats2_sum:
            return f"{pokemon1_name} ({stats1_sum} stats) es más fuerte que {pokemon2_name} ({stats2_sum} stats)."
        elif stats2_sum > stats1_sum:
            return f"{pokemon2_name} ({stats2_sum} stats) es más fuerte que {pokemon1_name} ({stats1_sum} stats)."
        else:
            return f"{pokemon1_name} ({stats1_sum} stats) y {pokemon2_name} ({stats2_sum} stats) tienen la misma fuerza."
