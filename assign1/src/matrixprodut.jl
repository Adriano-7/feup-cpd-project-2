function on_mult(m_ar::Int, m_br::Int)
    pha = fill(1.0, m_ar, m_ar)
    phb = [i + 1 for i in 0:m_br-1, j in 0:m_br-1]
    phc = zeros(m_ar, m_br)

    elapsed_time = @elapsed begin
        for i in 1:m_ar
            for j in 1:m_br
                temp = 0.0
                for k in 1:m_ar
                    temp += pha[i, k] * phb[k, j]  # Corrected this line
                end
                phc[i, j] = temp
            end
        end
    end
    
    println("Time: ", elapsed_time, " seconds")

    println("Result matrix:")
    for i in 1:min(10, m_br)
        print(phc[1, i], " ")
    end
    println()

    return elapsed_time
end


function on_mult_line(m_ar::Int, m_br::Int)
    pha = fill(1.0, m_ar, m_ar)
    phb = [i + 1 for i in 0:m_br-1, j in 0:m_br-1]
    phc = zeros(m_ar, m_ar)

    elapsed_time = @elapsed begin
        for i in 1:m_ar
            for k in 1:m_ar
                for j in 1:m_br
                    phc[i, j] += pha[i, k] * phb[k, j]
                end
            end
        end
    end

    println("Time: ", elapsed_time, " seconds")

    println("Result matrix:")
    for i in 1:min(10, m_br)
        print(phc[1, i], " ")
    end
    println()

    return elapsed_time
end

function main()
    outputFile = open("resultsMultJulia.csv", "w")
    println(outputFile, "Try,Dimension,Time")

    for trial in 0:9
        # From 600 to 3000, step 400 (OnMult)
        for dim in 600:400:3000
            println("Trial: $trial")
            println("Dimension: $dim")

            elapsed_time = on_mult(dim, dim)  

            # Write results to the CSV file
            println(outputFile, "$trial,$dim,$elapsed_time") 

            println()
        end
    end

    # From 600 to 3000, step 400 (OnMultLine)
    outputFile = open("resultsMultLineJulia.csv", "w")
    println(outputFile, "Try,Dimension,Time")

    for trial in 0:9
        for dim in 600:400:3000
            println("Trial: $trial")
            println("Dimension: $dim")

            elapsed_time = on_mult_line(dim, dim)  

            println(outputFile, "$trial,$dim,$elapsed_time")  

            println()
        end
    end
    close(outputFile)
end

main()
