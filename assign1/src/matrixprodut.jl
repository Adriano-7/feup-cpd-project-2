function on_mult(m_ar::Int, m_br::Int)
    pha = ones(Float64, m_ar, m_ar)
    phb = [Float64(i+1) for i in 0:m_br-1]
    phc = zeros(Float64, m_ar, m_br)

    elapsed_time = @elapsed begin
        for i in 1:m_ar
            for j in 1:m_br
                temp = 0.0
                for k in 1:m_ar
                    temp += pha[i, k] * phb[k]
                end
                phc[i, j] = temp
            end
        end
    end

    println("Elapsed time: ", elapsed_time, " seconds")

    println("Result matrix:")
    for i in 1:min(10, m_br)
        print(phc[1, i], " ")
    end
    println()
end

function on_mult_line(m_ar::Int, m_br::Int)
    pha = ones(Float64, m_ar, m_ar)
    phb = [Float64(i+1) for i in 0:m_br-1]
    phc = zeros(Float64, m_ar, m_br)

    elapsed_time = @elapsed begin
        for i in 1:m_ar
            for k in 1:m_ar
                temp = 0.0
                for j in 1:m_br
                    temp += pha[i, k] * phb[k]
                end
                phc[i, j] = temp
            end
        end
    end

    println("Elapsed time: ", elapsed_time, " seconds")

    println("Result matrix:")
    for i in 1:min(10, m_br)
        print(phc[1, i], " ")
    end
    println()
end

function on_mult_block(m_ar::Int, m_br::Int, block_size::Int)
    pha = ones(Float64, m_ar, m_ar)
    phb = [Float64(i+1) for i in 0:m_br-1]
    phc = zeros(Float64, m_ar, m_br)

    elapsed_time = @elapsed begin   
        for blockRowStart in 1:block_size:m_ar
            for blockColStart in 1:block_size:m_br
                for i in 1:m_ar
                    for j in blockColStart:min(blockColStart + block_size - 1, m_br)
                        temp = 0.0
                        for k in blockRowStart:min(blockRowStart + block_size - 1, m_ar)
                            temp += pha[i, k] * phb[k]
                        end
                        phc[i, j] = temp
                    end
                end
            end
        end
    end

    println("Elapsed time: ", elapsed_time, " seconds")

    println("Result matrix:")
    for i in 1:min(10, m_br)
        print(phc[1, i], " ")
    end
    println()
end

function main()
    op = 1

    #=
    for dim in 600:400:3000
        println("Dimensions: $dim")
        on_mult(dim, dim)

        println("\n\n")
    end
    =#

    for dim in 4096:2048:10240
        for block_size in [128, 256, 512]
            println("Dimensions: $dim")
            println("Block Size: $block_size")
            on_mult_block(dim, dim, block_size)

            println("\n\n")
        end
    end

    #=
    while op != 0
        println("\n1. Multiplication")
        println("2. Line Multiplication")
        println("3. Block Multiplication")
        print("Selection?: ")
        op = parse(Int, readline())

        if op == 0
            break
        end

        println("Dimensions: lins=cols ? ")
        lin = parse(Int, readline())
        col = lin

        if op == 1
            on_mult(lin, col)
        elseif op == 2
            on_mult_line(lin, col)
        elseif op == 3
            println("Block Size? ")
            block_size = parse(Int, readline())
            #on_mult_block(lin, col, block_size)
        else
            println("Invalid choice")
        end
    end
    =#
end

main()
