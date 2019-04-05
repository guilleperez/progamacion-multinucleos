%----------------------------------------------------------
% Práctica #7: MapReduce con Erlang
% Date: April 7, 2019.
% Authors:
%          A01370880 Rubén Escalante Chan
%          A01377162 Guillermo Pérez Trueba
%----------------------------------------------------------
-module(mapred).
-export([primes/2, sexy/2, apocalyptic/2, phi13/2]).
-import(plists, [mapreduce/2]). 

% UTILS
pow(_, 0) -> 1;
pow(A, B) when B > 0 ->
  A * pow(A, B - 1).
  
% 1
primes(S, E) ->
    D = plists:mapreduce(fun(X) ->  { is_prime(X), X} end, numeros(S, E)),
     case dict:find(true, D) of
        {ok, Value} -> Value;
        error->[]
    end.

is_prime(2) -> true;
is_prime(3) -> true;
is_prime(X) -> prime_number(X, X-1).

prime_number(_, 1) -> true;
prime_number(X, N) when X rem N /= 0 -> prime_number(X, N-1);
prime_number(_, _) -> false.

numeros(Limit, Limit) -> [Limit];
numeros(N, Limit) -> [N | numeros(N+1, Limit)].

% 2
apocalyptic(N, E) ->
  A = plists:mapreduce(fun (X) -> {is_cursed(X), X} end, lists:seq(N, E)),
  case dict:find(true, A) of
    {ok, Value} -> lists:sort(Value);
    error -> []
  end.

is_cursed(X) ->
  case string:str(integer_to_list(pow(2, X)), "666") of
    0 -> false;
    _ -> true
  end.

% 3
sexy(S,E)  ->  
    D = plists:mapreduce(fun(X) ->  
      {prime_and_prime(is_prime(X + 6), is_prime(X + 12)), {X, X + 6, X + 12} } end, primes(S,E)),
    case dict:find(true, D) of
       {ok, Value} -> Value;
       error->[]
    end.

prime_and_prime(true, true) -> true;
prime_and_prime(_, _) -> false.

% 4
phi13(S, E) ->
  A = plists:mapreduce(fun (X) -> {phi(X), X} end, lists:seq(S, E)),
  case dict:find(true, A) of
    {ok, Value} -> lists:sort(Value);
    error -> []
  end.

phi(X) ->
  Result = sum_list(integer_to_list(pow(2, X))),
  Result rem 13 == 0.

sum_list([H | []]) -> list_to_integer([H]);
sum_list([H | T]) -> list_to_integer([H]) + sum_list(T).