%----------------------------------------------------------
% Práctica #5: Erlang secuencial
% Date: March 17, 2019.
% Authors:
%          A01370880 Rubén Escalante Chan
%          A01377162 Guillermo Pérez Trueba
%----------------------------------------------------------

-module(sequential).
-export([but_last/1, merge/2, insert/2, sort/1, binary/1, bcd/1, prime_factors/1, compress/1, encode/1, decode/1]).

% 
% 1. The function but_last returns a list with the same elements as its input list but excluding the last element. 
% Assume that the input list contains at least one element.
%

but_last([_]) -> [];
but_last([H | T]) -> [H | but_last(T)].

%
% 2. The function merge returns the list that results from combining in ascending order all the
% elements contained in the two lists of numbers taken as arguments. The input lists should be in
% ascending order.
%

merge([], []) -> [];
merge(A, []) -> A;
merge([], B) -> B;
merge([A | Atail], [B | Btail]) -> 
  if
    A =< B ->
      [A | merge(Atail, [B | Btail])];
    B =< A ->
      [B | merge([A | Atail], Btail)]
  end.

%
% 3. The function insert takes two arguments: a number N and a list of numbers L in ascending order. 
% It returns a new list with the same elements as L but inserting N in its corresponding place.
%

insert(N, []) -> [N];
insert(N, [H | []]) when N =< H -> [N | [H]];
insert(N, [H | []]) when N > H -> [H | [N]];
insert(N, [H | T]) when N =< H -> [N | [H | T]];
insert(N, [H | T]) when N > H -> [H | insert(N, T)].

%
% 4. The function sort takes an unordered list of numbers as an argument, and returns a new list
% with the same elements but in ascending order. You must use the insert function defined in the
% previous problem.
%

sort([]) -> [];
sort([H | T]) -> insert(H, sort(T)).

%
% 5. The function binary takes an integer N as input (assume that N ≥ 0). If N is equal to zero, it returns an empty list. 
% If N is greater than zero, it returns a list with a sequence of ones and zeros equivalent to the binary representation of N.
%

binary(0) -> [];
binary(N) -> binary(N div 2) ++ [N rem 2].

%
% 6. The function bcd takes an integer N as input (assume that N ≥ 0), and returns a list of
% strings with the BCD representation of N. A BCD (Binary Coded Decimal) number is an encoding
% for decimal numbers in which each decimal digit is represented by its own 4-bit binary sequence.
%

bcd_binary(N) ->
  integer_to_list(N div 8 rem 2) ++ integer_to_list(N div 4 rem 2) ++ integer_to_list(N div 2 rem 2) ++ integer_to_list(N rem 2).

bcd(N) when N < 10 -> [bcd_binary(N)];
bcd(N) -> bcd(N div 10) ++ bcd(N rem 10).

%
% 7. The function prime_factors takes an integer N as input (assume that N > 0), and returns a list 
% containing the prime factors of N in ascending order. 
% The prime factors are the prime numbers that divide a number exactly.
%

prime_factors(N) -> prime_factors(N, 2).

prime_factors(1, _) -> [];
prime_factors(N, I) when N rem I == 0 ->  [I | prime_factors(N div I, I)];
prime_factors(N, I) when N rem I /= 0 ->  prime_factors(N, I+1).

%
% 8. The function compress takes a list Lst as its argument. If Lst contains consecutive repeated
% elements, they should be replaced with a single copy of the element. The order of the elements
% should not be changed.
%

compress([H | []]) -> [H];
compress([H | [H2 | T]]) when H == H2 -> compress([H2 | T]);
compress([H | [H2 | T]]) when H /= H2 -> [H | compress([H2 | T])].

%
% 9. The function encode takes a list Lst as its argument. Consecutive duplicates of elements in Lst are 
% encoded as tuples {N, E} where N is the number of duplicates of the element E. If an element has no duplicates, 
% it is simply copied into the result list. 
%

encode([H | T]) -> encode(H, T, 1).

encode(First, [], 1) -> [First |  []];
encode(First, [], Count) -> [list_to_tuple([ Count | [First] ])] ++  [];
encode(First, [H | T], 1) when First /= H -> [First] ++ encode(H, T, 1);
encode(First, [H | T], Count) when First /= H -> [list_to_tuple([ Count | [First] ])] ++ encode(H, T, 1);
encode(First, [H | T], Count) when First == H -> encode(H, T, Count+1).

%
% 10. The function decode takes as its argument an encoded list Lst like the output from the
% previous problem. It returns the decoded version of Lst.
%

repeat({0, _}) -> [];
repeat({N, A}) when N > 0 -> [A | repeat({N - 1, A})];
repeat(A) -> [A].

decode([H | []]) -> repeat(H);
decode([H | T]) -> repeat(H) ++ decode(T).